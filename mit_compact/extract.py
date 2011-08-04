# -*- coding: utf-8 -*-
import os, glob, pickle
from numpy import *

elevs = glob.glob('elev*')
data = {}
for elev in elevs:
    e = int(elev[4:])
    data[e] = {}
    for f in os.listdir(elev):
        a = int(f[-8:-5])
        d = fromfile(os.path.join(elev, f), dtype='>i2')
        d.shape = (128,2)
        data[e][a] = d[:,0].astype(float32)
        data[e][360-a] = d[:,1].astype(float32)
        
fd = open('data.pickle', 'w')
pickle.dump(data, fd)
fd.close()