import json
import requests

from django.http import JsonResponse
from rest_framework.views import APIView

from users.models import User
from config.settings import SERVER_KEY


# /message/
class SendToTopic(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))
        from_user = data['userID']
        to_topic = data['topic']
        message = data['message']

        finalmsg = {
            'to': '/topics/' + to_topic,
            'data': {
                'message': message,
                'from_user': from_user,
            }
        }

        headers = {
            'Authorization': 'key=' + SERVER_KEY,
            'Content-Type': "application/json",
        }

        fcm_response = requests.post('https://fcm.googleapis.com/fcm/send', json.dumps(finalmsg), headers=headers)
        response = {}

        fcm_json_response = json.loads(fcm_response.content)
        if fcm_json_response.has_key('error'):
            response['success'] = False
        else:
            response['success'] = True
            response['message_id'] = fcm_json_response.get('message_id')

        return JsonResponse(response)

