#!/bin/bash

#!/bin/bash

VERSION_libusb=
URL_libusb=
MD5_libusb=
BUILD_libusb=$SRC_PATH/jni/libusb
RECIPE_libusb=$RECIPES_PATH/libusb

function prebuild_libusb() {
	true
}

function build_libusb() {
	cd $SRC_PATH/jni
	push_arm
	try ndk-build V=1 libusb
	pop_arm
}

function postbuild_libusb() {
	true
}

