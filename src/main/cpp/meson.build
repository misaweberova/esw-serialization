boost_regex_dep = meson.get_compiler('cpp').find_library('boost_regex')
thread_dep = dependency('threads')
jsoncpp_dep = dependency('jsoncpp')
protobuf_dep = dependency('protobuf')
avro_dep = meson.get_compiler('cpp').find_library('avrocpp')
protoc = find_program('protoc', required : true)
gen = generator(protoc,
                output    : ['@BASENAME@.pb.cc', '@BASENAME@.pb.h'],
                arguments : ['--proto_path=@CURRENT_SOURCE_DIR@/../proto', '--cpp_out=@BUILD_DIR@', '@INPUT@'])
generated = gen.process('../proto/measurements.proto')

avrogencpp = find_program('avrogencpp', required : true)
run_command('avrogencpp', '--input=../avro/measurements.avsc', '--output=measurements.av.hh', '-n eswavro', check : true)

srcs = ['dataset.cpp', 'main.cpp', 'measurementinfo.cpp', 'result.cpp', generated, 'measurements.av.hh']
executable('server', srcs,
  dependencies: [thread_dep, boost_regex_dep, jsoncpp_dep, protobuf_dep, avro_dep])
