#!/bin/bash

VERSION_alembic=0.6.0
URL_alembic=https://pypi.python.org/packages/source/a/alembic/alembic-${VERSION_alembic}.tar.gz
DEPS_alembic=(setuptools sqlalchemy mako)
MD5_alembic=
BUILD_alembic=$BUILD_PATH/alembic/$(get_directory $URL_alembic)
RECIPE_alembic=$RECIPES_PATH/alembic

function prebuild_alembic() {
	true
}

function build_alembic() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/alembic" ]; then
		return
	fi

	cd $BUILD_alembic

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	export PYTHONPATH=$SITEPACKAGES_PATH
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $BUILD_hostpython/hostpython setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/twisted/test
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/twisted/*/test

	unset LDSHARED

	pop_arm
}

function postbuild_alembic() {
	true
}

