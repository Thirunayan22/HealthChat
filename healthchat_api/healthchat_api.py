import flask
import spacy
import pandas as pd
import numpy as np
from flask import request, jsonify, send_file, send_from_directory, abort, Flask
from flask_ngrok import run_with_ngrok
import urllib.request
from werkzeug.utils import secure_filename

app = Flask(__name__)
run_with_ngrok(app)
app.secret_key = "secret_key"

pd.set_option('max_colwidth', 100)  # Increase column width
data = pd.read_excel("WHO_FAQ.xlsx")
data.head()

import tensorflow as tf
import tensorflow_hub as hub
import tensorflow_text
import re

def preprocess_sentences(input_sentences):
    return [re.sub(r'(covid-19|covid)', 'coronavirus', input_sentence, flags=re.I) 
            for input_sentence in input_sentences]
        
# Load module containing USE
module = hub.load('https://tfhub.dev/google/universal-sentence-encoder-multilingual-qa/3')

# Create response embeddings
response_encodings = module.signatures['response_encoder'](
        input=tf.constant(preprocess_sentences(data.Answer)),
        context=tf.constant(preprocess_sentences(data.Context)))['outputs']

@app.route('/',methods=['GET'])
def return_home():
  return "<h1>Chat API</h1>"

@app.route('/chat',methods=['POST'])
def chat_response():
  if(request.method == "POST"):
    text_message = [request.args.get('text')]
    print("\nPassed value : ",text_message)
    print("Passed value type : ",type(text_message))

    question_encodings = module.signatures['question_encoder'](
      tf.constant(preprocess_sentences(text_message))
    )['outputs']

    responses = data.Answer[np.argmax(np.inner(question_encodings,response_encodings),axis=1)]
    responses = list(responses)[0]
    print(responses)
    return  responses

if(__name__ == "__main__"):
  app.run()



