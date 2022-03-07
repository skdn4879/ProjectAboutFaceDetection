from pyfcm import FCMNotification

APIKEY = "AAAA46h66HM:APA91bHWqXn6WCTRFnUGcSN92IQ8-_dyZJSAQn1c5zxUKjHQMxbaY0ou3hjnNTS7VR5v-erS765fYTR-t6-YF_UTJjISe8482AlHqOnj9yuR9f23BzO_SxXGIl5hgzyh4P54YAvtotuX"
TOKEN = "c0TeAF5tR725-0_gCHRC22:APA91bGxMAeSld2uJ4Vnym9GXsLaqdkwdUpOP6NzY-2RIlAHwn3ekcndy7ipecskxj2FyMY3fIB0XcqMXsSzr9C6og1SS2cdYeAUfTIXHT1Lqx_j6bTavDL7-gafJk7SuLWqtPKoZa39"

push_service = FCMNotification(APIKEY)

def sendMessage(body, title):
    data_message = {
        "contents": body,
        "title": title
        }
    
    result = push_service.single_device_data_message(registration_id=TOKEN, data_message=data_message)
    
    print(result)
    
sendMessage("Someone Came to your House", "House Notice")