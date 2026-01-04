import io
import wrapper
import os

def test_add_signature_twice():
    # 1. 最初の署名
    sample_image = "../sample/ChatGPT_Image.png"
    if not os.path.exists(sample_image):
        print(f"Sample image {sample_image} not found")
        return

    with open(sample_image, "rb") as f:
        img_data = f.read()

    stream1 = io.BytesIO(img_data)
    signed_data1 = wrapper.sign(stream1, "image/png", title="First Signature")
    
    # 2. 2回目の署名
    stream2 = io.BytesIO(signed_data1)
    signed_data2 = wrapper.sign(stream2, "image/png", title="Second Signature")

    # 3. 検証
    stream_verify = io.BytesIO(signed_data2)
    result = wrapper.verify(stream_verify, "image/png")
    
    import json
    parsed_result = json.loads(result)
    
    # マニフェストの数を確認（通常、既存のものを保持していれば、複数のマニフェストが存在するか、ingredientsに含まれる）
    manifests = parsed_result.get("manifests", {})
    print(f"Number of manifests: {len(manifests)}")
    
    # active_manifestを取得
    active_manifest_label = parsed_result.get("active_manifest")
    active_manifest = manifests.get(active_manifest_label, {})
    
    print(f"Active manifest title: {active_manifest.get('title')}")
    
    # ingredientsを確認
    ingredients = active_manifest.get("ingredients", [])
    print(f"Number of ingredients: {len(ingredients)}")
    for i, ing in enumerate(ingredients):
        print(f"Ingredient {i} title: {ing.get('title')}")

    if len(ingredients) > 0:
        print("Success: Previous signature is kept as an ingredient.")
    else:
        print("Failure: Previous signature is not kept.")

if __name__ == "__main__":
    test_add_signature_twice()
