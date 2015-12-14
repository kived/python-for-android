
from pythonforandroid.toolchain import PythonRecipe


class ZopeComponentRecipe(PythonRecipe):
    name = 'zope_component'
    version = '4.2.2'
    url = 'https://pypi.python.org/packages/source/z/zope.component/zope.component-{version}.tar.gz'
    
    depends = ['python2', 'setuptools', 'zope', 'zope_event']
    
    call_hostpython_via_targetpython = False


recipe = ZopeComponentRecipe()
