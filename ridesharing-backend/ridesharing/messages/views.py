import json

import requests
from django.http import JsonResponse
from rest_framework.views import APIView

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


# /usertopics
class GetUserTopics(APIView):
    def get(self, request):
        user_id = request.GET.get('userID')
        user = User.objects.get(id=user_id)

        headers = {
            'Authorization': 'key=' + SERVER_KEY,
        }

        params = {
            'details': True,
        }

        fcm_response = requests.get('https://iid.googleapis.com/iid/info/' + user.reg_token, params=params, headers=headers)

        fcm_json_response = json.loads(fcm_response.content)

        return JsonResponse(fcm_json_response.get('rel').get('topics'))
