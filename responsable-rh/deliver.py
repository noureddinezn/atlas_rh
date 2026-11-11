from pathlib import Path
import json
root = Path(__file__).parent
items = []
def request(name, method, path, body=None, status=200, capture=None):
    req = {'method': method, 'header': [], 'url': '{{baseUrl}}' + path}
    if body is not None:
        req['header'] = [{'key':'Content-Type','value':'application/json'}]
        req['body'] = {'mode':'raw','raw':json.dumps(body, indent=2)}
    tests = [f'pm.test("HTTP {status}", () => pm.response.to.have.status({status}));']
    if capture:
        tests.append(f'if (pm.response.code === {status}) pm.collectionVariables.set("{capture}", pm.response.json().id);')
    items.append({'name':name,'request':req,'event':[{'listen':'test','script':{'type':'text/javascript','exec':tests}}]})
department = {'name':'Engineering {{$guid}}','description':'Engineering team'}
request('Create department','POST','/api/departments',department,201,'departmentId')
request('List departments','GET','/api/departments?page=0&size=10&sort=name,asc')
request('Get department','GET','/api/departments/{{departmentId}}')
request('Update department','PUT','/api/departments/{{departmentId}}',{'name':'Engineering {{$guid}}','description':'Updated'})
employee = {'matricule':'EMP-{{$guid}}','email':'{{$guid}}@example.com','firstName':'Sara','lastName':'Amrani','departmentId':'{{departmentId}}','managerId':None}
request('Create manager','POST','/api/employees',employee,201,'managerId')
employee = dict(employee, firstName='Youssef', managerId='{{managerId}}')
request('Create employee','POST','/api/employees',employee,201,'employeeId')
request('Search employees','GET','/api/employees?name=Youssef&departmentId={{departmentId}}&page=0&size=10&sort=lastName,asc')
request('Get employee','GET','/api/employees/{{employeeId}}')
request('Manager team','GET','/api/employees/{{managerId}}/team?page=0&size=10')
request('Reject self-management','PUT','/api/employees/{{employeeId}}',dict(employee,managerId='{{employeeId}}'),400)
request('Reject occupied department deletion','DELETE','/api/departments/{{departmentId}}',status=409)
request('Reject CDD without endDate','POST','/api/contracts',{'employeeId':'{{employeeId}}','type':'CDD','startDate':'2026-01-01'},400)
contract = {'employeeId':'{{employeeId}}','type':'CDD','startDate':'2026-01-01','endDate':'2026-12-31'}
request('Create contract','POST','/api/contracts',contract,201,'contractId')
request('Reject second active contract','POST','/api/contracts',contract,409)
request('Get contract','GET','/api/contracts/{{contractId}}')
request('Contract history','GET','/api/contracts/employee/{{employeeId}}?page=0&size=10&sort=startDate,desc')
request('Terminate contract','PATCH','/api/contracts/{{contractId}}/terminate',{'terminationDate':'2026-10-30'})
request('Reject repeated termination','PATCH','/api/contracts/{{contractId}}/terminate',{'terminationDate':'2026-10-30'},409)
request('Create replacement CDI','POST','/api/contracts',{'employeeId':'{{employeeId}}','type':'CDI','startDate':'2026-10-31'},201)
request('Reject deletion with history','DELETE','/api/employees/{{employeeId}}',status=409)
request('Missing department','GET','/api/departments/9223372036854775807',status=404)
request('Invalid department','POST','/api/departments',{'name':''},400)
collection = {'info':{'name':'Atlas RH - US1 US2 US3','schema':'https://schema.getpostman.com/json/collection/v2.1.0/collection.json'},'variable':[{'key':'baseUrl','value':'http://localhost:8080'}],'item':items}
(root/'Atlas-RH.postman_collection.json').write_text(json.dumps(collection, indent=2),encoding='utf-8')
paths = [root/'pom.xml'] + sorted((root/'src').rglob('*.java')) + sorted((root/'src/main/resources').rglob('*')) + [root/'compose.yaml']
parts = ['# Complete Responsable RH source code\n\nEach heading is the relative destination path. All files are also available separately.\n']
for path in paths:
    if not path.is_file(): continue
    language = {'.java':'java','.xml':'xml','.sql':'sql','.yml':'yaml','.yaml':'yaml'}.get(path.suffix,'text')
    parts.append(f'## `{path.relative_to(root).as_posix()}`\n\n```{language}\n{path.read_text(encoding="utf-8").rstrip()}\n```\n')
(root/'COMPLETE-CODE.md').write_text('\n'.join(parts),encoding='utf-8')
