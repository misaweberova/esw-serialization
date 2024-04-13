#ifndef MEASUREMENTINFO_H
#define MEASUREMENTINFO_H

#include <string>

#include "jsonserializable.h"
#include <json/json.h>

class MeasurementInfo : public JsonSerializable
{
   private:
   int id;
   Json::Int64 timestamp;
   std::string measurerName;

   public:
   MeasurementInfo();
   MeasurementInfo(int id, long timestamp, std::string measurerName);

   void Serialize(Json::Value &root);
   void Deserialize(Json::Value &root);

   int getId() const;
   void setId(int value);
   long getTimestamp() const;
   void setTimestamp(long value);
   std::string getMeasurerName() const;
   void setMeasurerName(const std::string &value);
};

#endif // MEASUREMENTINFO_H
