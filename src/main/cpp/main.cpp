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
   throw std::logic_error("TODO: Implement avro");

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
   }
   /* Calculate averages */
   /* Serialize averages */
   /* Send the result back */

   delete[] buff;
}

void processProtobuf(tcp::iostream &stream)
{
   throw std::logic_error("TODO: Implement protobuf");
   /* Read data from stream */
   /* Unserialize data */
   /* Calculate averages */
   /* Serialize averages */
   /* Send the result back */
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
