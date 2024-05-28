#include "result.h"

MeasurementInfo Result::getInfo() const
{
    return info;
}

void Result::setInfo(const MeasurementInfo &value)
{
    info = value;
}

std::map<DataType, double > Result::getAverages() const
{
    return averages;
}

void Result::setAverages(const std::map<DataType, double> &value)
{
    averages = value;
}

Result::Result()
{

}

Result::Result(MeasurementInfo info, const std::map<DataType, std::vector<double> > &data)
{
    this->info = info;
    for (auto it=data.begin(); it!=data.end(); ++it){
        double average = 0;
        for(auto val=it->second.begin(); val != it->second.end(); ++val){
            average += *val;
        }
        averages.insert(std::make_pair(it->first, average/it->second.size()));
    }
}

void Result::Serialize(Json::Value &root)
{
    info.Serialize(root["info"]);
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

        root["averages"][typeS] = averages[typeE];
    }
}

void Result::Deserialize(Json::Value &root)
{

}
