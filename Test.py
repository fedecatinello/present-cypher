from Present import Present

key_80 = "00000000000000000000".decode('hex')
key_128 = "0123456789abcdef0123456789abcdef".decode('hex')

cipher = Present(key_80)

filetype = ".txt"

plainpieces = []
encryptedblocks = []

print "ENCRYPTING"
plainfile = open("test" + filetype, 'rb')
encryptedfile = open("encrypted" + filetype, 'ab')
encryptedfile.truncate()
while True:
    piece = plainfile.read(8)
    if not piece:
        break
    if len(piece) < 8:
        print "ADDING PADDING"
        piece = piece.ljust(8, '\0')
    encryptedblocks += [cipher.encrypt(piece)]
    encryptedfile.write(cipher.encrypt(piece))
plainfile.close()
encryptedfile.close()

print "DECRYPTING AND WRITING"
decryptedfile = open("result" + filetype, 'ab')
decryptedfile.truncate()
for encryptedblock in encryptedblocks:
    decryptedresult = cipher.decrypt(encryptedblock)
    decryptedfile.write(decryptedresult)
decryptedfile.close()

print "FINISHED"
