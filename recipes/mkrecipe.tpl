#!/bin/bash

VERSION_#NAME#=
URL_#NAME#=https://pypi.python.org/packages/source/#NAME1#/#NAME#/#NAME#-${VERSION_#NAME#}.tar.gz
DEPS_#NAME#=()
MD5_#NAME#=
BUILD_#NAME#=$BUILD_PATH/#NAME#/$(get_directory $URL_#NAME#)
RECIPE_#NAME#=$RECIPES_PATH/#NAME#

function prebuild_#NAME#() {
	true
}

function shouldbuild_#NAME#() {
	if [ -d "$SITEPACKAGES_PATH/#NAME#" ]; then
		DO_BUILD=0
	fi
}

function build_#NAME#() {
	cd $BUILD_#NAME#

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $HOSTPYTHON setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	# If you need to remove any files:
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/#NAME#/<file names...>

	unset LDSHARED

	pop_arm
}

function postbuild_#NAME#() {
	true
}

