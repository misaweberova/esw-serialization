#ifndef RESULT_H
#define RESULT_H

#include <map>
#include <vector>

#include "measurementinfo.h"
#include "datatype.h"
#include <json/json.h>
#include "jsonserializable.h"

class Result : public JsonSerializable
{
private:
    MeasurementInfo info;
    std::map<DataType, double> averages;

public:
    Result();
    Result(MeasurementInfo info, const std::map<DataType, std::vector<double> > &data);

    void Serialize( Json::Value& root );
    void Deserialize( Json::Value& root );

    MeasurementInfo getInfo() const;
    void setInfo(const MeasurementInfo &value);
    std::map<DataType, double> getAverages() const;
    void setAverages(const std::map<DataType, double> &value);
};

#endif // RESULT_H
