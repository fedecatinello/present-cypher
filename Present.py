# Python PRESENT implementation
# Version: 1.0
# Date: 13/10/2008
#
# =============================================================================
# Copyright (c) 2008 Christophe Oosterlynck (christophe.oosterlynck@gmail.com)
#                    Philippe Teuwen (philippe.teuwen@nxp.com)
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
# =============================================================================

""" PRESENT block cipher implementation

USAGE EXAMPLE:
---------------
Importing:
-----------
>>> from pypresent import Present

Encrypting with a 80-bit key:
------------------------------
>>> key = "00000000000000000000".decode('hex')
>>> plain = "0000000000000000".decode('hex')
>>> cipher = Present(key)
>>> encrypted = cipher.encrypt(plain)
>>> encrypted.encode('hex')
'5579c1387b228445'
>>> decrypted = cipher.decrypt(encrypted)
>>> decrypted.encode('hex')
'0000000000000000'

Encrypting with a 128-bit key:
-------------------------------
>>> key = "0123456789abcdef0123456789abcdef".decode('hex')
>>> plain = "0123456789abcdef".decode('hex')
>>> cipher = Present(key)
>>> encrypted = cipher.encrypt(plain)
>>> encrypted.encode('hex')
'0e9d28685e671dd6'
>>> decrypted = cipher.decrypt(encrypted)
>>> decrypted.encode('hex')
'0123456789abcdef'

fully based on standard specifications: http://www.crypto.ruhr-uni-bochum.de/imperia/md/content/texte/publications/conferences/present_ches2007.pdf
test vectors: http://www.crypto.ruhr-uni-bochum.de/imperia/md/content/texte/publications/conferences/slides/present_testvectors.zip
"""
class Present:

    def __init__(self,key,rounds=32):
        """Create a PRESENT cipher object

        key:    the key as a 128-bit or 80-bit rawstring
        rounds: the number of rounds as an integer, 32 by default
        """
        self.rounds = rounds
        if len(key) * 8 == 80:
            self.roundkeys = generateRoundkeys80(string2number(key),self.rounds)
        elif len(key) * 8 == 128:
            self.roundkeys = generateRoundkeys128(string2number(key),self.rounds)
        else:
            raise ValueError, "Key must be a 128-bit or 80-bit rawstring"

    def encrypt(self,block):
        """Encrypt 1 block (8 bytes)

        Input:  plaintext block as raw string
        Output: ciphertext block as raw string
        """
        state = string2number(block)
        for i in xrange (self.rounds-1):
            state = addRoundKey(state,self.roundkeys[i])
            state = sBoxLayer(state)
            state = pLayer(state)
        cipher = addRoundKey(state,self.roundkeys[-1])
        return number2string_N(cipher,8)

    def decrypt(self,block):
        """Decrypt 1 block (8 bytes)

        Input:  ciphertext block as raw string
        Output: plaintext block as raw string
        """
        state = string2number(block)
        for i in xrange (self.rounds-1):
            state = addRoundKey(state,self.roundkeys[-i-1])
            state = pLayer_dec(state)
            state = sBoxLayer_dec(state)
        decipher = addRoundKey(state,self.roundkeys[0])
        return number2string_N(decipher,8)

    def get_block_size(self):
        return 8

#        0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f
Sbox= [0xc,0x5,0x6,0xb,0x9,0x0,0xa,0xd,0x3,0xe,0xf,0x8,0x4,0x7,0x1,0x2]
Sbox_inv = [Sbox.index(x) for x in xrange(16)]
PBox = [0,16,32,48,1,17,33,49,2,18,34,50,3,19,35,51,
        4,20,36,52,5,21,37,53,6,22,38,54,7,23,39,55,
        8,24,40,56,9,25,41,57,10,26,42,58,11,27,43,59,
        12,28,44,60,13,29,45,61,14,30,46,62,15,31,47,63]
PBox_inv = [PBox.index(x) for x in xrange(64)]

def generateRoundkeys80(key,rounds):
    """Generate the roundkeys for a 80-bit key

    Input:
            key:    the key as a 80-bit integer
            rounds: the number of rounds as an integer
    Output: list of 64-bit roundkeys as integers"""
    roundkeys = []
    for i in xrange(1,rounds+1): # (K1 ... K32)
        # rawkey: used in comments to show what happens at bitlevel
        # rawKey[0:64]
        roundkeys.append(key >>16)
        #1. Shift
        #rawKey[19:len(rawKey)]+rawKey[0:19]
        key = ((key & (2**19-1)) << 61) + (key >> 19)
        #2. SBox
        #rawKey[76:80] = S(rawKey[76:80])
        key = (Sbox[key >> 76] << 76)+(key & (2**76-1))
        #3. Salt
        #rawKey[15:20] ^ i
        key ^= i << 15
    return roundkeys

def generateRoundkeys128(key,rounds):
    """Generate the roundkeys for a 128-bit key

    Input:
            key:    the key as a 128-bit integer
            rounds: the number of rounds as an integer
    Output: list of 64-bit roundkeys as integers"""
    roundkeys = []
    for i in xrange(1,rounds+1): # (K1 ... K32)
        # rawkey: used in comments to show what happens at bitlevel
        roundkeys.append(key >>64)
        #1. Shift
        key = ((key & (2**67-1)) << 61) + (key >> 67)
        #2. SBox
        key = (Sbox[key >> 124] << 124)+(Sbox[(key >> 120) & 0xF] << 120)+(key & (2**120-1))
        #3. Salt
        #rawKey[62:67] ^ i
        key ^= i << 62
    return roundkeys

def addRoundKey(state,roundkey):
    return state ^ roundkey

def sBoxLayer(state):
    """SBox function for encryption

    Input:  64-bit integer
    Output: 64-bit integer"""

    output = 0
    for i in xrange(16):
        output += Sbox[( state >> (i*4)) & 0xF] << (i*4)
    return output

def sBoxLayer_dec(state):
    """Inverse SBox function for decryption

    Input:  64-bit integer
    Output: 64-bit integer"""
    output = 0
    for i in xrange(16):
        output += Sbox_inv[( state >> (i*4)) & 0xF] << (i*4)
    return output

def pLayer(state):
    """Permutation layer for encryption

    Input:  64-bit integer
    Output: 64-bit integer"""
    output = 0
    for i in xrange(64):
        output += ((state >> i) & 0x01) << PBox[i]
    return output

def pLayer_dec(state):
    """Permutation layer for decryption

    Input:  64-bit integer
    Output: 64-bit integer"""
    output = 0
    for i in xrange(64):
        output += ((state >> i) & 0x01) << PBox_inv[i]
    return output

def string2number(i):
    """ Convert a string to a number

    Input: string (big-endian)
    Output: long or integer
    """
    return int(i.encode('hex'),16)

def number2string_N(i, N):
    """Convert a number to a string of fixed size

    i: long or integer
    N: length of string
    Output: string (big-endian)
    """
    s = '%0*x' % (N*2, i)
    return s.decode('hex')

def _test():
    import doctest
    doctest.testmod()

if __name__ == "__main__":
    _test()
