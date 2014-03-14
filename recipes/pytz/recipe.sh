#!/bin/bash

VERSION_pytz=2013.9
URL_pytz=https://pypi.python.org/packages/source/p/pytz/pytz-${VERSION_pytz}.tar.bz2
DEPS_pytz=(python)
MD5_pytz=
BUILD_pytz=$BUILD_PATH/pytz/$(get_directory $URL_pytz)
RECIPE_pytz=$RECIPES_PATH/pytz

function prebuild_pytz() {
	true
}

function shouldbuild_pytz() {
	if [ -d "$SITEPACKAGES_PATH/pytz" ]; then
		DO_BUILD=0
	fi
}

function build_pytz() {
	cd $BUILD_pytz

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $HOSTPYTHON setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	# If you need to remove any files:
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/pytz/<file names...>

	unset LDSHARED

	pop_arm
}

function postbuild_pytz() {
	true
}

