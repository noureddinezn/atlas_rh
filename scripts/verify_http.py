import json
import re
import uuid
import urllib.request
import urllib.error
from pathlib import Path

root = Path(__file__).resolve().parents[1]
collection = json.loads((root / 'AtlasRH.postman_collection.json').read_text(encoding='utf-8'))
variables = {v['key']: v['value'] for v in collection['variable']}
variables['baseUrl'] = 'http://localhost:18081'

def resolve(value):
    return re.sub(r'\{\{([^}]+)\}\}', lambda m: str(uuid.uuid4()) if m[1] == '$guid' else str(variables[m[1]]), value)

for item in collection['item']:
    source = item['request']
    body = resolve(source['body']['raw']).encode() if 'body' in source else None
    request = urllib.request.Request(resolve(source['url']), data=body, method=source['method'],
                                     headers={h['key']:h['value'] for h in source['header']})
    try:
        response = urllib.request.urlopen(request, timeout=20)
    except urllib.error.HTTPError as error:
        response = error
    payload = response.read().decode()
    script = '\n'.join(item['event'][0]['script']['exec'])
    expected = int(re.search(r'have.status\((\d+)\)', script)[1])
    assert response.code == expected, f"{item['name']}: expected {expected}, received {response.code}: {payload}"
    capture = re.search(r'collectionVariables.set\("([^"]+)"', script)
    if capture:
        variables[capture[1]] = json.loads(payload)['id']
    print(f"PASS {response.code} {item['name']}")
print(f"{len(collection['item'])} HTTP scenarios passed")
