import json
import os

from flask import Flask, request, jsonify, make_response

import wrapper

app = Flask(__name__)

os.environ['C2PA_NO_VERIFY'] = '1'

@app.post("/verify")
def verify():
    f = request.files.get('image')
    if not f:
        return jsonify(error='file is required'), 400
    return jsonify(json.loads(wrapper.verify(f, f.mimetype))), 200


@app.post("/sign")
def sign():
    f = request.files.get('image')
    if not f:
        return jsonify(error='file is required'), 400
    img_bin = wrapper.sign(
        f,
        f.mimetype,
        title=request.form.get('title'),
        ai_inference=request.form.get('ai_inference'),
        ai_inference_constraints_info=request.form.get('ai_inference_constraints_info'),
        ai_generative_training=request.form.get('ai_generative_training'),
        ai_generative_training_constraints_info=request.form.get('ai_generative_training_constraints_info')
    )
    response = make_response(img_bin)
    response.headers.set('Content-Type', f.mimetype)
    return response


@app.errorhandler(Exception)
def handle_exception(error):
    # HTTPExceptionの場合
    if hasattr(error, 'code'):
        return jsonify(
            error=error.name,
            message=error.description
        ), error.code

    # その他の例外
    return jsonify(
        error='Internal Server Error',
        message=str(error)
    ), 500


if __name__ == '__main__':
    app.run(debug=True)
