# -*- coding: utf-8 -*-
import os, glob, pickle
from numpy import *

spkrInv = fromfile('headphones+spkr/Opti-minphase.dat', dtype='>i2').astype(float32) / 32768.

elevs = glob.glob('elev*')
data = {}
for elev in elevs:
    e = int(elev[4:])
    data[e] = {}
    for f in glob.glob(elev + '/L*'):
        a = int(f[-8:-5])
        d = convolve(fromfile(f, dtype='>i2'), spkrInv)
        #d.shape = ()
        data[e][a] = d[26:180].astype(float32)
        
fd = open('data.pickle', 'w')
pickle.dump(data, fd)
fd.close()