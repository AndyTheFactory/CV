import argparse
import cv2
import os
import glob
import cvlib as cv
import numpy as np


ap = argparse.ArgumentParser()
ap.add_argument("-d", "--dataset", required=True,
	help="path to input dataset (i.e., directory of images)")

args = ap.parse_args()

# load image files from the dataset
image_files = [f for f in glob.glob(args.dataset + "/**/*.jpg", recursive=True) if not os.path.isdir(f)]

for imgfile in image_files:
    image = cv2.imread(imgfile)
    face, confidence = cv.detect_face(image)

    if (len(face)!=1):
        continue

    (startX, startY, endX, endY) = face[0][0], face[0][1], face[0][2], face[0][3]

    face_crop = np.copy(image[startY:endY,startX:endX])

    face_crop = cv2.resize(face_crop, (96,96))

    crop,ext=os.path.splitext(imgfile);
    crop+="_crop."+ext

    cv2.imwrite(crop,face_crop)
