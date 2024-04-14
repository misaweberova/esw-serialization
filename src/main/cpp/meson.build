boost_regex_dep = meson.get_compiler('cpp').find_library('boost_regex')
thread_dep = dependency('threads')
jsoncpp_dep = dependency('jsoncpp')

# TODO: debug file generation

protoc = find_program('protoc', required : true)
gen_proto = generator(protoc,
                output    : ['@BASENAME@.pb.cc', '@BASENAME@.pb.h'],
                arguments : ['--proto_path=@CURRENT_SOURCE_DIR@', '--cpp_out=@BUILD_DIR@', '@INPUT@'])
generated_proto = gen_proto.process('../proto/measurements.proto')

avrogencpp = find_program('avrogencpp', required : true)
gen_avro = generator(avrogencpp,
                output    : ['@BASENAME@.hh'],
                arguments : ['-i @INPUT@', '-o cpx.hh', '-n c'])
generated_avro = gen_avro.process('../avro/measurements.avsc')

proto_dep = dependency('protobuf')
avro_dep = meson.get_compiler('cpp').find_library('avrocpp')

srcs = [generated_proto, generated_avro, 'dataset.cpp', 'main.cpp', 'measurementinfo.cpp', 'result.cpp']
executable('server', srcs, dependencies: [thread_dep, boost_regex_dep, jsoncpp_dep, proto_dep, avro_dep])