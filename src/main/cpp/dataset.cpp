#include "dataset.h"

#include <iostream>
MeasurementInfo Dataset::getInfo() const
{
    return info;
}

void Dataset::setInfo(const MeasurementInfo &value)
{
    info = value;
}

std::map<DataType, std::vector<double> > Dataset::getRecords() const
{
    return records;
}

void Dataset::setRecords(const std::map<DataType, std::vector<double> > &value)
{
    records = value;
}

Dataset::Dataset()
{

}

void Dataset::Serialize(Json::Value &root)
{

}

void Dataset::Deserialize(Json::Value &root)
{
    info.Deserialize(root["info"]);
    const Json::Value& recordsJ = root["records"];
    for ( int typeI = DOWNLOAD; typeI <= PING; typeI++ )
    {
        DataType typeE = static_cast<DataType>(typeI);
        std::string typeS;
        switch (typeE) {
        case DOWNLOAD:
            typeS = "DOWNLOAD";
            break;
        case UPLOAD:
            typeS = "UPLOAD";
            break;
        case PING:
            typeS = "PING";
            break;
        default:
            typeS = "";
            break;
        }

        std::vector<double> data;
        const Json::Value& typeV = recordsJ[typeS];
        for (unsigned i = 0; i < typeV.size(); i++){
            data.push_back(typeV[i].asDouble());
        }

        records.insert(std::make_pair(typeE,data));
    }
}
