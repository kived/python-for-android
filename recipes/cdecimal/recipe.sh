#!/bin/bash

VERSION_cdecimal=2.3
URL_cdecimal=http://www.bytereef.org/software/mpdecimal/releases/cdecimal-${VERSION_cdecimal}.tar.gz
DEPS_cdecimal=(python)
MD5_cdecimal=
BUILD_cdecimal=$BUILD_PATH/cdecimal/$(get_directory $URL_cdecimal)
RECIPE_cdecimal=$RECIPES_PATH/cdecimal

function prebuild_cdecimal() {
	true
}

function build_cdecimal() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/cdecimal" ]; then
		return
	fi

	cd $BUILD_cdecimal

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	cp $RECIPE_cdecimal/locale.h $BUILD_cdecimal/locale.h
	sed -i 's/#include <locale.h>/#include "locale.h"/' $BUILD_cdecimal/io.c

	./configure --host x86_64

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $BUILD_PATH/python-install/bin/python.host setup.py build_ext -v
	try $BUILD_PATH/python-install/bin/python.host setup.py install -O2

	# If you need to remove any files:
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/cdecimal/<file names...>

	unset LDSHARED

	pop_arm
}

function postbuild_cdecimal() {
	true
}

