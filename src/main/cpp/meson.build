boost_regex_dep = meson.get_compiler('cpp').find_library('boost_regex')
thread_dep = dependency('threads')
jsoncpp_dep = dependency('jsoncpp')
proto_dep = dependency('protobuf')
avro_dep = meson.get_compiler('cpp').find_library('avrocpp')

# Generate files for protobuff
avrogencpp = find_program('avrogencpp', required : true)
run_command('avrogencpp', '--input=../avro/measurements.avsc', '--output=measurements.hh', '-n c', check: true)

# Generate files for avro
protoc = find_program('protoc', required : true)
run_command('protoc', '--proto_path=../../../src/main/proto', '--cpp_out=../../../src/main/cpp/', '../../../src/main/proto/measurements.proto', check: true)


srcs = ['dataset.cpp', 'main.cpp', 'measurementinfo.cpp', 'result.cpp', 'measurements.pb.cc', 'measurements.pb.h', 'measurements.hh']
executable('server', srcs, dependencies: [thread_dep, boost_regex_dep, jsoncpp_dep, proto_dep, avro_dep])
