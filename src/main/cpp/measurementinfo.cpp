#include "measurementinfo.h"

int MeasurementInfo::getId() const
{
    return id;
}

void MeasurementInfo::setId(int value)
{
    id = value;
}

long MeasurementInfo::getTimestamp() const
{
    return timestamp;
}

void MeasurementInfo::setTimestamp(long value)
{
    timestamp = value;
}

std::string MeasurementInfo::getMeasurerName() const
{
    return measurerName;
}

void MeasurementInfo::setMeasurerName(const std::string &value)
{
    measurerName = value;
}

MeasurementInfo::MeasurementInfo()
{

}


MeasurementInfo::MeasurementInfo(int id, long timestamp, std::string measurerName) : id(id), timestamp(timestamp), measurerName(measurerName)
{

}

void MeasurementInfo::Serialize(Json::Value &root)
{
    root["id"] = id;
    root["timestamp"] = timestamp;
    root["measurerName"] = measurerName;
}

void MeasurementInfo::Deserialize(Json::Value &root)
{
    id = root.get("id", 0).asInt();
    timestamp = root.get("timestamp", 0).asInt64();
    measurerName = root.get("measurerName", "").asString();
}
