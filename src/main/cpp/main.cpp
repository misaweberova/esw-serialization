#include <iostream>

#include <json/json.h>

#include "dataset.h"
#include "result.h"

#include <boost/algorithm/string.hpp>
#include <boost/asio.hpp>

#include "cpx.hh"
#include <memory>

using namespace std;
using boost::asio::ip::tcp;

void processJSON(tcp::iostream &stream)
{
   Json::Value val;
   Json::Reader reader;

   std::vector<Dataset> datasets;
   std::vector<Result> results;

   /* Read json string from the stream */
   string s;
   getline(stream, s, '\0');

   /* Parse string */
   reader.parse(s, val);

   datasets.clear();
   results.clear();
   for (int i = 0; i < val.size(); i++) {
      datasets.emplace_back();
      datasets[i].Deserialize(val[i]);
      /* Calculate averages */
      results.emplace_back(datasets[i].getInfo(), datasets[i].getRecords());
   }

   /* Create output JSON structure */
   Json::Value out;
   //    Json::FastWriter writer;
   Json::StyledWriter writer;
   for (int i = 0; i < results.size(); i++) {
      Json::Value result;
      results[i].Serialize(result);
      out[i] = result;
   }

   /* Send the result back */
   std::string output = writer.write(out);
   stream << output;
   if (!getenv("ESW_JSON_QUIET"))
      cout << output;
   else
      cout << "Quiet operation enabled" << endl;
}

void processAvro(tcp::iostream &stream)
{
   // throw std::logic_error("TODO: Implement avro");

   /* Get message size */
   unsigned int message_size;
   char size_bytes[4];
   stream.read(size_bytes, 4);
   std::memcpy(&message_size, size_bytes, sizeof(int));
   message_size = ntohl(message_size);

   /* Read data from stream */
   char *buff = new char[message_size];
   stream.read(buff, message_size);

   a::AMeasurementsResponse response;
   a::AMeasurementsRequest request;

   std::unique_ptr<avro::InputStream> stream_in_p =
       avro::memoryInputStream((uint8_t *)buffer, message_size);
   avro::DecoderPtr decoder = avro::binaryDecoder();
   decoder->init(*stream_in_p);

   /* Unserialize data */
   avro::decode(*decoder, request);

   for (auto curr : request.requestTuple) {

      /* Get records data from request (download, upload and ping) */
      double download_count = 0;
      double upload_count = 0;
      double ping_count = 0;

      for (unsigned j = 0; j < curr.records.DOWNLOAD.size(); j++) {
         download_count += curr.records.DOWNLOAD[j];
         upload_count += curr.records.UPLOAD[j];
         ping_count += curr.records.PING[j];
      }

      /* Calculate averages */
      a::AAverage average;
      average.DOWNLOAD = download_count / curr.records.DOWNLOAD.size();
      average.UPLOAD = upload_count / curr.records.UPLOAD.size();
      average.PING = ping_count / curr.records.PING.size();

      /* Serialize averages */
      a::AResponseTuple tmp;
      tmp.average = average;
      tmp.measurementInfo = curr.measurementInfo;
      response.responseTuple.push_back(tmp);
   }

   /* Send the result back */
   std::unique_ptr<avro::OutputStream> outStream =
       avro::ostreamOutputStream(stream);
   avro::EncoderPtr encoder = avro::binaryEncoder();
   encoder->init(*outStream);

   avro::encode(*encoder, response);
   encoder->flush();

   delete[] buff;
}

void processProtobuf(tcp::iostream &stream)
{
   // throw std::logic_error("TODO: Implement protobuf");

   /* Get message size */
   unsigned int message_size;
   char size_bytes[4];
   stream.read(size_bytes, 4);
   std::memcpy(&message_size, size_bytes, sizeof(int));
   message_size = ntohl(message_size);

   /* Read data from stream */
   char *buff = new char[message_size];
   stream.read(buff, message_size);

   esw::PMeasurementsResponse response;
   esw::PMeasurementsRequest request;
   request.ParseFromArray(buff, message_size);

   /* Unserialize data */
   avro::decode(*decoder, request);

   for (int i = 0; i < request.requesttuple_size(); ++i) {

      const esw::PMeasurementsRequest_RequestTuple &requestTuple =
          request.requesttuple(i);
      /* Get records data from request (download, upload and ping) */
      const esw::Records &records = requestTuple.records();
      double download_count = 0;
      double upload_count = 0;
      double ping_count = 0;

      for (int j = 0; j < records.download_size(); ++j) {
         download_count += records.download(j);
         upload_count += records.upload(j);
         ping_count += records.ping(j);
      }

      /* Calculate averages */
      auto *averages = new esw::Average();
      averages->set_download(downloads / records.download_size());
      averages->set_upload(uploads / records.upload_size());
      averages->set_ping(pings / records.ping_size());

      /* Recreate measurement info*/
      auto *info = new esw::MeasurementInfo();
      info->set_id(requestTuple.measurementinfo().id());
      info->set_timestamp(requestTuple.measurementinfo().timestamp());
      info->set_measurername(requestTuple.measurementinfo().measurername());

      /* Create result with computed average */
      esw::MeasurementsResponse_ResponseTuple *responseTuple =
          response.add_responsetuple();
      responseTuple->set_allocated_measurementinfo(info);
      responseTuple->set_allocated_average(averages);
   }

   /* Serialize averages & send the result back */
   response.SerializeToOstream(&stream);

   delete[] buff;
}

int main(int argc, char *argv[])
{

   if (argc != 3) {
      cout << "Error: two arguments required - ./server  <port> <protocol>"
           << endl;
      return 1;
   }

   // unsigned short int port = 12345;
   unsigned short int port = atoi(argv[1]);

   // std::string protocol = "json";
   std::string protocol(argv[2]);
   boost::to_upper(protocol);
   try {
      boost::asio::io_service io_service;

      tcp::endpoint endpoint(tcp::v4(), port);
      tcp::acceptor acceptor(io_service, endpoint);

      while (true) {
         cout << "Waiting for message in " + protocol + " format..." << endl;
         tcp::iostream stream;
         boost::system::error_code ec;
         acceptor.accept(*stream.rdbuf(), ec);

         if (protocol == "JSON") {
            processJSON(stream);
         } else if (protocol == "AVRO") {
            processAvro(stream);
         } else if (protocol == "PROTO") {
            processProtobuf(stream);
         } else {
            throw std::logic_error("Protocol not yet implemented");
         }
      }

   } catch (std::exception &e) {
      std::cerr << "Exception: " << e.what() << std::endl;
      return 1;
   }

   return 0;
}
