#!/bin/bash

VERSION_mako=0.8.1
URL_mako=https://pypi.python.org/packages/source/M/Mako/Mako-${VERSION_mako}.tar.gz
DEPS_mako=(setuptools)
MD5_mako=
BUILD_mako=$BUILD_PATH/mako/$(get_directory $URL_mako)
RECIPE_mako=$RECIPES_PATH/mako

function prebuild_mako() {
	true
}

function build_mako() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/mako" ]; then
		return
	fi

	cd $BUILD_mako

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $BUILD_hostpython/hostpython setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/twisted/test
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/twisted/*/test

	unset LDSHARED

	pop_arm
}

function postbuild_mako() {
	true
}

