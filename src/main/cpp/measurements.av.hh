/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#ifndef MEASUREMENTS_AV_HH_77461518__H_
#define MEASUREMENTS_AV_HH_77461518__H_


#include <sstream>
#include "boost/any.hpp"
#include "avro/Specific.hh"
#include "avro/Encoder.hh"
#include "avro/Decoder.hh"

namespace  eswavro {
struct AVInfo {
    int64_t id;
    std::string measurerName;
    int64_t timestamp;
    AVInfo() :
        id(int64_t()),
        measurerName(std::string()),
        timestamp(int64_t())
        { }
};

struct AVAvgData {
    AVInfo info;
    double download;
    double upload;
    double ping;
    AVAvgData() :
        info(AVInfo()),
        download(double()),
        upload(double()),
        ping(double())
        { }
};

struct AVAvgDataArray {
    std::vector<AVAvgData > dataArray;
    AVAvgDataArray() :
        dataArray(std::vector<AVAvgData >())
        { }
};

struct AVMeasurement {
    AVInfo info;
    std::vector<double > download;
    std::vector<double > upload;
    std::vector<double > ping;
    AVMeasurement() :
        info(AVInfo()),
        download(std::vector<double >()),
        upload(std::vector<double >()),
        ping(std::vector<double >())
        { }
};

struct AVMeasurementArray {
    std::vector<AVMeasurement > datasets;
    AVMeasurementArray() :
        datasets(std::vector<AVMeasurement >())
        { }
};

struct _measurements_avsc_Union__0__ {
private:
    size_t idx_;
    boost::any value_;
public:
    size_t idx() const { return idx_; }
    AVInfo get_AVInfo() const;
    void set_AVInfo(const AVInfo& v);
    AVAvgData get_AVAvgData() const;
    void set_AVAvgData(const AVAvgData& v);
    AVAvgDataArray get_AVAvgDataArray() const;
    void set_AVAvgDataArray(const AVAvgDataArray& v);
    AVMeasurement get_AVMeasurement() const;
    void set_AVMeasurement(const AVMeasurement& v);
    AVMeasurementArray get_AVMeasurementArray() const;
    void set_AVMeasurementArray(const AVMeasurementArray& v);
    _measurements_avsc_Union__0__();
};

inline
AVInfo _measurements_avsc_Union__0__::get_AVInfo() const {
    if (idx_ != 0) {
        throw avro::Exception("Invalid type for union");
    }
    return boost::any_cast<AVInfo >(value_);
}

inline
void _measurements_avsc_Union__0__::set_AVInfo(const AVInfo& v) {
    idx_ = 0;
    value_ = v;
}

inline
AVAvgData _measurements_avsc_Union__0__::get_AVAvgData() const {
    if (idx_ != 1) {
        throw avro::Exception("Invalid type for union");
    }
    return boost::any_cast<AVAvgData >(value_);
}

inline
void _measurements_avsc_Union__0__::set_AVAvgData(const AVAvgData& v) {
    idx_ = 1;
    value_ = v;
}

inline
AVAvgDataArray _measurements_avsc_Union__0__::get_AVAvgDataArray() const {
    if (idx_ != 2) {
        throw avro::Exception("Invalid type for union");
    }
    return boost::any_cast<AVAvgDataArray >(value_);
}

inline
void _measurements_avsc_Union__0__::set_AVAvgDataArray(const AVAvgDataArray& v) {
    idx_ = 2;
    value_ = v;
}

inline
AVMeasurement _measurements_avsc_Union__0__::get_AVMeasurement() const {
    if (idx_ != 3) {
        throw avro::Exception("Invalid type for union");
    }
    return boost::any_cast<AVMeasurement >(value_);
}

inline
void _measurements_avsc_Union__0__::set_AVMeasurement(const AVMeasurement& v) {
    idx_ = 3;
    value_ = v;
}

inline
AVMeasurementArray _measurements_avsc_Union__0__::get_AVMeasurementArray() const {
    if (idx_ != 4) {
        throw avro::Exception("Invalid type for union");
    }
    return boost::any_cast<AVMeasurementArray >(value_);
}

inline
void _measurements_avsc_Union__0__::set_AVMeasurementArray(const AVMeasurementArray& v) {
    idx_ = 4;
    value_ = v;
}

inline _measurements_avsc_Union__0__::_measurements_avsc_Union__0__() : idx_(0), value_(AVInfo()) { }
}
namespace avro {
template<> struct codec_traits< eswavro::AVInfo> {
    static void encode(Encoder& e, const  eswavro::AVInfo& v) {
        avro::encode(e, v.id);
        avro::encode(e, v.measurerName);
        avro::encode(e, v.timestamp);
    }
    static void decode(Decoder& d,  eswavro::AVInfo& v) {
        if (avro::ResolvingDecoder *rd =
            dynamic_cast<avro::ResolvingDecoder *>(&d)) {
            const std::vector<size_t> fo = rd->fieldOrder();
            for (std::vector<size_t>::const_iterator it = fo.begin();
                it != fo.end(); ++it) {
                switch (*it) {
                case 0:
                    avro::decode(d, v.id);
                    break;
                case 1:
                    avro::decode(d, v.measurerName);
                    break;
                case 2:
                    avro::decode(d, v.timestamp);
                    break;
                default:
                    break;
                }
            }
        } else {
            avro::decode(d, v.id);
            avro::decode(d, v.measurerName);
            avro::decode(d, v.timestamp);
        }
    }
};

template<> struct codec_traits< eswavro::AVAvgData> {
    static void encode(Encoder& e, const  eswavro::AVAvgData& v) {
        avro::encode(e, v.info);
        avro::encode(e, v.download);
        avro::encode(e, v.upload);
        avro::encode(e, v.ping);
    }
    static void decode(Decoder& d,  eswavro::AVAvgData& v) {
        if (avro::ResolvingDecoder *rd =
            dynamic_cast<avro::ResolvingDecoder *>(&d)) {
            const std::vector<size_t> fo = rd->fieldOrder();
            for (std::vector<size_t>::const_iterator it = fo.begin();
                it != fo.end(); ++it) {
                switch (*it) {
                case 0:
                    avro::decode(d, v.info);
                    break;
                case 1:
                    avro::decode(d, v.download);
                    break;
                case 2:
                    avro::decode(d, v.upload);
                    break;
                case 3:
                    avro::decode(d, v.ping);
                    break;
                default:
                    break;
                }
            }
        } else {
            avro::decode(d, v.info);
            avro::decode(d, v.download);
            avro::decode(d, v.upload);
            avro::decode(d, v.ping);
        }
    }
};

template<> struct codec_traits< eswavro::AVAvgDataArray> {
    static void encode(Encoder& e, const  eswavro::AVAvgDataArray& v) {
        avro::encode(e, v.dataArray);
    }
    static void decode(Decoder& d,  eswavro::AVAvgDataArray& v) {
        if (avro::ResolvingDecoder *rd =
            dynamic_cast<avro::ResolvingDecoder *>(&d)) {
            const std::vector<size_t> fo = rd->fieldOrder();
            for (std::vector<size_t>::const_iterator it = fo.begin();
                it != fo.end(); ++it) {
                switch (*it) {
                case 0:
                    avro::decode(d, v.dataArray);
                    break;
                default:
                    break;
                }
            }
        } else {
            avro::decode(d, v.dataArray);
        }
    }
};

template<> struct codec_traits< eswavro::AVMeasurement> {
    static void encode(Encoder& e, const  eswavro::AVMeasurement& v) {
        avro::encode(e, v.info);
        avro::encode(e, v.download);
        avro::encode(e, v.upload);
        avro::encode(e, v.ping);
    }
    static void decode(Decoder& d,  eswavro::AVMeasurement& v) {
        if (avro::ResolvingDecoder *rd =
            dynamic_cast<avro::ResolvingDecoder *>(&d)) {
            const std::vector<size_t> fo = rd->fieldOrder();
            for (std::vector<size_t>::const_iterator it = fo.begin();
                it != fo.end(); ++it) {
                switch (*it) {
                case 0:
                    avro::decode(d, v.info);
                    break;
                case 1:
                    avro::decode(d, v.download);
                    break;
                case 2:
                    avro::decode(d, v.upload);
                    break;
                case 3:
                    avro::decode(d, v.ping);
                    break;
                default:
                    break;
                }
            }
        } else {
            avro::decode(d, v.info);
            avro::decode(d, v.download);
            avro::decode(d, v.upload);
            avro::decode(d, v.ping);
        }
    }
};

template<> struct codec_traits< eswavro::AVMeasurementArray> {
    static void encode(Encoder& e, const  eswavro::AVMeasurementArray& v) {
        avro::encode(e, v.datasets);
    }
    static void decode(Decoder& d,  eswavro::AVMeasurementArray& v) {
        if (avro::ResolvingDecoder *rd =
            dynamic_cast<avro::ResolvingDecoder *>(&d)) {
            const std::vector<size_t> fo = rd->fieldOrder();
            for (std::vector<size_t>::const_iterator it = fo.begin();
                it != fo.end(); ++it) {
                switch (*it) {
                case 0:
                    avro::decode(d, v.datasets);
                    break;
                default:
                    break;
                }
            }
        } else {
            avro::decode(d, v.datasets);
        }
    }
};

template<> struct codec_traits< eswavro::_measurements_avsc_Union__0__> {
    static void encode(Encoder& e,  eswavro::_measurements_avsc_Union__0__ v) {
        e.encodeUnionIndex(v.idx());
        switch (v.idx()) {
        case 0:
            avro::encode(e, v.get_AVInfo());
            break;
        case 1:
            avro::encode(e, v.get_AVAvgData());
            break;
        case 2:
            avro::encode(e, v.get_AVAvgDataArray());
            break;
        case 3:
            avro::encode(e, v.get_AVMeasurement());
            break;
        case 4:
            avro::encode(e, v.get_AVMeasurementArray());
            break;
        }
    }
    static void decode(Decoder& d,  eswavro::_measurements_avsc_Union__0__& v) {
        size_t n = d.decodeUnionIndex();
        if (n >= 5) { throw avro::Exception("Union index too big"); }
        switch (n) {
        case 0:
            {
                 eswavro::AVInfo vv;
                avro::decode(d, vv);
                v.set_AVInfo(vv);
            }
            break;
        case 1:
            {
                 eswavro::AVAvgData vv;
                avro::decode(d, vv);
                v.set_AVAvgData(vv);
            }
            break;
        case 2:
            {
                 eswavro::AVAvgDataArray vv;
                avro::decode(d, vv);
                v.set_AVAvgDataArray(vv);
            }
            break;
        case 3:
            {
                 eswavro::AVMeasurement vv;
                avro::decode(d, vv);
                v.set_AVMeasurement(vv);
            }
            break;
        case 4:
            {
                 eswavro::AVMeasurementArray vv;
                avro::decode(d, vv);
                v.set_AVMeasurementArray(vv);
            }
            break;
        }
    }
};

}
#endif
