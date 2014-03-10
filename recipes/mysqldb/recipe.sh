#!/bin/bash

VERSION_mysqldb=1.2.4
P4A_mysqldb_DIR=`dirname $0`/../../../aeris2-android/src/mysqldb/
DEPS_mysqldb=(setuptools sqlalchemy)
MD5_mysqldb=
BUILD_mysqldb=$BUILD_PATH/mysqldb/mysqldb
RECIPE_mysqldb=$RECIPES_PATH/mysqldb

function prebuild_mysqldb() {
	true
}

function build_mysqldb() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/MySQLdb" ]; then
		rm -rf "$BUILD_PATH/python-install/lib/python2.7/site-packages/MySQLdb"
	fi

	cd $BUILD_mysqldb

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	rm -rf $BUILD_mysqldb/build

	$BUILD_PATH/python-install/bin/python.host setup.py build_ext
	try $BUILD_PATH/python-install/bin/python.host setup.py build_ext -v
	try find build/lib.* -name "*.o" -exec $STRIP {} \;
	try $BUILD_PATH/python-install/bin/python.host setup.py install -O2

	# If you need to remove any files:
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/mysqldb/<file names...>

	unset LDSHARED

	pop_arm
}

function postbuild_mysqldb() {
	true
}

