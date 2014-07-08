#!/bin/bash

VERSION_pyusb=1.0.0b1
URL_pyusb=https://pypi.python.org/packages/source/p/pyusb/pyusb-$VERSION_pyusb.tar.gz
#P4A_aeris2_DIR=`dirname $0`/../../../aeris2-android/src/aeris2/
DEPS_pyusb=(python)
MD5_pyusb=
#BUILD_aeris2=$BUILD_PATH/aeris2/$(get_directory $URL_aeris2)
BUILD_pyusb=$BUILD_PATH/pyusb/$(get_directory $URL_pyusb)
RECIPE_pyusb=$RECIPES_PATH/pyusb

function prebuild_pyusb() {
	true
}

function shouldbuild_pyusb() {
	if [ -d "$SITEPACKAGES_PATH/pyusb" ]; then
		DO_BUILD=0
	fi
}

function build_pyusb() {
	cd $BUILD_pyusb
    
    # check marker in our source build
	if [ -f .patched ]; then
		# no patch needed
		return
	fi

	try patch -p1 < $RECIPE_pyusb/patches/fix-android.patch
	
	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

    try $HOSTPYTHON setup.py install -O2
    
	unset LDSHARED

	pop_arm
	
	touch .patched
}

function postbuild_pyusb() {
	true
}
