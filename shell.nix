let
  sources = import ./npins;
  pkgs = import sources.nixpkgs {};
in
with pkgs;
mkShell {
  packages = [
    pkg-config
    meson
    ninja
    boost
    jsoncpp
    jdk17
    maven
    protobuf
    avro-cpp
  ];
}
