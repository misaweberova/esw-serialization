#ifndef JSONSERIALIZABLE_H
#define JSONSERIALIZABLE_H

#include <json/json.h>

class JsonSerializable
{
public:
   virtual ~JsonSerializable( void ) {}
   virtual void Serialize( Json::Value& root ) =0;
   virtual void Deserialize( Json::Value& root) =0;
};

#endif // JSONSERIALIZABLE_H
