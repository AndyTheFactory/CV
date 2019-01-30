# import necessary packages
from keras.preprocessing.image import img_to_array
from keras.models import load_model
from keras.utils import get_file
import numpy as np
import argparse
import cv2
import os
import glob
from scipy.io import loadmat
import json
import cvlib as cv
import csv

# handle command line arguments
ap = argparse.ArgumentParser()
ap.add_argument("-p", "--path", required=True,
	help="path to test data set")
args = ap.parse_args()

# download pre-trained model file (one-time download)
dwnld_link = "https://s3.ap-south-1.amazonaws.com/arunponnusamy/pre-trained-weights/gender_detection.model"
model_path = get_file("gender_detection.model", dwnld_link,
                      cache_subdir="pre-trained", cache_dir=os.getcwd())

# load model
model = load_model(model_path)

mat = loadmat(args.path+'/wiki.mat ')

csv_file = open(args.path+'/wiki.csv', mode='w',newline='')
csvwriter = csv.DictWriter(csv_file, fieldnames=['Filename', 'NrFaces', 'Gender',
                                                    'NrDetFaces', 'GenderDet', 'Confidence','IsDifferent'], delimiter='\t')
csvwriter.writeheader()
total = len(mat['wiki']['full_path'][0][0][0])
for i in range(total):
    if (i%1000 == 1):
        print("Progress: %.2f %%" % float(i*100/total))
    w = {
        "filename":mat['wiki']['full_path'][0][0][0][i][0],
        "gender":mat['wiki']['gender'][0][0][0][i],
        "nrfaces":len(mat['wiki']['face_location'][0][0][0][i]),
        "faces":mat['wiki']['face_location'][0][0][0][i].tolist()
    }
    # w.filename=mat['wiki']['full_path'][0][0][0][i][0]
    # w.gender=mat['wiki']['gender'][0][0][0][i]
    # w.faces=mat['wiki']['face_location'][0][0][0][i]
    imgfile=args.path+'/'+w["filename"]
    if (not os.path.isfile(imgfile)):
        continue
    jsonfile,ext=os.path.splitext(w["filename"]);
    jsonfile=args.path+'/'+jsonfile+'.json';
    with open(jsonfile,'w') as textfile:
        json.dump(w, textfile, indent=4, sort_keys=False)

    if (w["nrfaces"]!=1):
        continue
    img = cv2.imread(imgfile)
    if (img is None):
        continue

    
    face, confidence = cv.detect_face(img)

    if (len(face)!=1):
        continue
    try:
        (startX, startY, endX, endY) = face[0][0], face[0][1], face[0][2], face[0][3]

        face_crop = np.copy(img[startY:endY,startX:endX])

        face_crop = cv2.resize(face_crop, (96,96))
        face_crop = face_crop.astype("float") / 255.0
        face_crop = img_to_array(face_crop)
        face_crop = np.expand_dims(face_crop, axis=0)

        conf = model.predict(face_crop)[0] #man,woman
        gender = abs(np.argmax(conf)-1) #wiki is woman=0, man=1
        maxconf = max(conf)


        csvwriter.writerow({
            'Filename' : w["filename"],
            'NrFaces' : w["nrfaces"],
            'Gender' : w["gender"],
            'NrDetFaces':len(face),
            'GenderDet':gender,
            'Confidence':maxconf,
            'IsDifferent': "0" if (gender==w["gender"]) else "1"
        })
        csv_file.flush()
    except:
        continue
csv_file.close()


