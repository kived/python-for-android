
from pythonforandroid.toolchain import PythonRecipe


class ZopeEventRecipe(PythonRecipe):
    name = 'zope_event'
    version = '4.1.0'
    url = 'https://pypi.python.org/packages/source/z/zope.event/zope.event-{version}.tar.gz'
    
    depends = ['python2', 'setuptools']
    
    call_hostpython_via_targetpython = False


recipe = ZopeEventRecipe()
