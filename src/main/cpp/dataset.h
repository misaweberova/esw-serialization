#ifndef DATASET_H
#define DATASET_H

#include <map>
#include <vector>
#include "measurementinfo.h"
#include "datatype.h"
#include <json/json.h>
#include "jsonserializable.h"

class Dataset : public JsonSerializable
{
private:
    MeasurementInfo info;
    std::map<DataType, std::vector<double>> records;
public:
    Dataset();

    void Serialize( Json::Value& root );
    void Deserialize( Json::Value& root );

    MeasurementInfo getInfo() const;
    void setInfo(const MeasurementInfo &value);
    std::map<DataType, std::vector<double> > getRecords() const;
    void setRecords(const std::map<DataType, std::vector<double> > &value);
};

#endif // DATASET_H
