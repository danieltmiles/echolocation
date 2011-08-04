#!/usr/bin/python
import alsaaudio
import sys, time, glob, os, re
from numpy import *
from PyQt4 import QtCore, QtGui

def mitFullData():
    print "running mit full"
    # Read in the MIT data
    baseDir = '/home/dmiles/src/android/play/mit_full'
    # dtype='>i2' means big-endian 16-bit integer
    # 32768 == 2**15

    # this makes it into a float that goes from -1 to 1. It has to be in that
    # range for the convolution because otherwise it would cause scaling

    # spkr maybe means speaker?
    spkrInv = fromfile(os.path.join(baseDir, 'headphones+spkr', 'Opti-minphase.dat'), dtype='>i2').astype(float32) / 32768.

    # glob is about wildcard filenames
    elevs = glob.glob(os.path.join(baseDir, 'elev*'))
    data = {}
    for elev in elevs:
        e = int(re.search(r'(-?\d+)', elev).groups()[0])
        data[e] = {}
        for f in glob.glob(os.path.join(elev, 'L*')):
            if not f == "%s/elev0/L0e115a.dat" % baseDir:
                continue
            a = int(f[-8:-5])
            ff = fromfile(f, dtype='>i2')
            d = convolve(ff, spkrInv)
            #d.shape = ()
            data[e][a] = d[26:180].astype(float32)
            for thing in ff:
                print thing

mitFullData()
