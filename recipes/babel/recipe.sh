#!/bin/bash

VERSION_babel=1.3
URL_babel=https://pypi.python.org/packages/source/B/Babel/Babel-${VERSION_babel}.tar.gz
DEPS_babel=(python)
MD5_babel=
BUILD_babel=$BUILD_PATH/babel/Babel-${VERSION_babel}
RECIPE_babel=$RECIPES_PATH/babel

function prebuild_babel() {
	true
}

function build_babel() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/Babel" ]; then
		return
	fi

	cd $BUILD_babel
	
	if [ -f .patched ]; then
	      # no patch needed
	      return
	fi

	try patch -p1 < $RECIPE_babel/patches/fix_setup.patch
	
	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $BUILD_hostpython/hostpython setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	unset LDSHARED

	pop_arm
	
	touch .patched
}

function postbuild_babel() {
	true
}

