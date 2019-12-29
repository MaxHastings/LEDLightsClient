#!/usr/bin/env python3
from __future__ import division
import socket
import time
from neopixel import *
import argparse
import thread
import json

# LED strip configuration:
LED_COUNT      = 180      # Number of LED pixels.
LED_PIN        = 18      # GPIO pin connected to the pixels (18 uses PWM!).
#LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_FREQ_HZ    = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA        = 10      # DMA channel to use for generating signal (try 10)
LED_BRIGHTNESS = 255     # Set to 0 for darkest and 255 for brightest
LED_INVERT     = False   # True to invert the signal (when using NPN transistor level shift)
LED_CHANNEL    = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53

HOST = '192.168.1.217'  # Standard loopback interface address (localhost)
PORT = 65432        # Port to listen on (non-privileged ports are > 1023)

global currentColor #intensity can be a maximum value of 255
currentColor = Color(128, 0, 0)

# Create NeoPixel object with appropriate configuration.
strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL)
# Intialize the library (must be called once before other functions).
strip.begin()

global length, frequency
length = 3
frequency = 50

global CMD_UPDATE_COLOR, CMD_UPDATE_FREQUENCY, CMD_LENGTH
CMD_UPDATE_COLOR = "color"
CMD_UPDATE_FREQUENCY = "frequency"
CMD_LENGTH = "length"
		
def waitForConnection():
	global currentColor, frequency, length
	global CMD_UPDATE_COLOR, CMD_UPDATE_FREQUENCY, CMD_LENGTH
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.bind((HOST, PORT))
		s.listen(5)
		conn, addr = s.accept()
		print('Connected by', addr)
		while True:
			data = conn.recv(1024)
			try:
				obj = json.loads(data)
				cmd = obj["cmd"]
				if(cmd == CMD_UPDATE_COLOR):
					red = obj["red"]
					green = obj["green"]
					blue = obj["blue"]
					print(red, green, blue)
					currentColor = Color(green, red, blue)
					#print(currentColor)
				elif(cmd == CMD_UPDATE_FREQUENCY):
					frequency = obj["frequency"]
					print("Frequency Change", frequency)
				elif(cmd == CMD_LENGTH):
					length = obj["length"]
					
			except Exception as e:
				print(e)
				print(data)
			if not data:
				s.close()
				conn.close()
				currentColor = Color(0,0,0)
				break
	except socket.error, exc:
		"Caught exception socket.error : %s" % exc
		
def loopStep():
	while True:
		global currentColor, frequency
		step()
		strip.show()
		time.sleep(1 / frequency)
				
def step2():
	numPixels = strip.numPixels()
	for i in range(numPixels):
		strip.setPixelColor(i, currentColor)

def step():
	numPixels = strip.numPixels()
	middleLED = int(numPixels / 2)
	
	margin = int(length / 2)
		
	for i in range(0, middleLED - margin, 1): #Move pixels to the left
		rightOf = i + length
		strip.setPixelColor(i, strip.getPixelColor(rightOf))
		
	for i in range(numPixels, middleLED - margin, -1): #Move pixels to the right
		leftOf = i - length
		strip.setPixelColor(i, strip.getPixelColor(leftOf))
	
	for i in range(0, margin + 1, 1):
		leftOf = middleLED - i;
		rightOf = middleLED + i;
		strip.setPixelColor(leftOf, currentColor)
		strip.setPixelColor(rightOf, currentColor)
			
def getRedValue(color):
	redMask = 0xFF
	return int(color & redMask)

def getGreenValue(color):
	greenMask = 0xFF00
	return int((color & greenMask) >> 8)

def getBlueValue(color):
	blueMask = 0xFF0000
	return int((color & blueMask) >> 16)
	
# Define functions which animate LEDs in various ways.
def colorWipe(strip, color, wait_ms=50):
    """Wipe color across display a pixel at a time."""
    for i in range(strip.numPixels()):
        strip.setPixelColor(i, color)
        strip.show()
        time.sleep(wait_ms/1000.0)

thread.start_new_thread(loopStep, ())
while True:
	print("Waiting for Connection")
	waitForConnection()
	print("Connection dropped")