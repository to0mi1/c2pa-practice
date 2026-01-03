import io

import c2pa
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import ec

with open("./cert/es256_certs.pem", "rb") as cert_file:
    certs = cert_file.read()
with open("./cert/es256_private.key", "rb") as key_file:
    key = key_file.read()

print("c2pa version:")
version = c2pa.sdk_version()
print(version)


def callback_signer_es256(data: bytes) -> bytes:
    """Callback function that signs data using ES256 algorithm."""
    private_key = serialization.load_pem_private_key(
        key,
        password=None,
        backend=default_backend()
    )
    signature = private_key.sign(
        data,
        ec.ECDSA(hashes.SHA256())
    )
    return signature


def sign(stream, mime_type, title='No Title') -> bytes:
    manifest = {
        "claim_generator": "C2PA Example",
        "claim_generator_info": [{
            "name": "C2PA Example",
            "version": "0.0.1",
        }],
        # Claims version 2 is the default, so the version
        # number can be omitted.
        # "claim_version": 2,
        "format": mime_type,
        "title": title,
        "ingredients": [],
        "assertions": [
            {
                "label": "c2pa.actions",
                "data": {
                    "actions": [
                        {
                            "action": "c2pa.created",
                            "digitalSourceType": "http://cv.iptc.org/newscodes/digitalsourcetype/digitalCreation"
                        }
                    ]
                }
            }
        ]
    }

    with c2pa.Signer.from_callback(
            callback=callback_signer_es256,
            alg=c2pa.C2paSigningAlg.ES256,
            certs=certs.decode('utf-8'),
            tsa_url="http://timestamp.digicert.com"
    ) as signer:
        with c2pa.Builder(manifest) as builder, io.BytesIO() as dest_io:
            builder.sign(
                signer=signer,
                format=mime_type,
                source=stream,
                dest=dest_io
            )
            return dest_io.read()


def verify(stream, mime_type=None):
    with c2pa.Reader(mime_type, stream) as reader:
        return reader.json()
