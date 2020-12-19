#
# Simple Python client that can be used to benchmark the server
#

import sys, base64, json, requests
from multiprocessing import Pool

payload = ""

def do_request(n):
    r = requests.post("http://127.0.0.1:8081/validate", data = payload)
    return r.text

if __name__ == "__main__":

    with open(sys.argv[1], 'rb') as h:
        content = base64.b64encode(h.read())

    data = {
        'data': content.decode('ascii'),
    }

    payload = json.dumps(data)

    # Switch between single request, and benchmark mode
    if len(sys.argv) == 2:
        print(do_request(payload))
    else:
        with Pool(16) as p:
            nreq = int(sys.argv[2])
            print("Performing %d requests ..." % nreq, end = '')
            p.map(do_request, range(1, nreq))
            print("done")
        

    
    
