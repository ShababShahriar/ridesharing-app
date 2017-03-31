import json

import requests
from django.http import JsonResponse
from django.forms.models import model_to_dict
from rest_framework.views import APIView

from config.settings import SERVER_KEY
from ridesharing.users.models import User
from ridesharing.bookings.models import Booking
from ridesharing.messages.models import Message


# /addmessage/
class AddNewMessage(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        data['from_user'] = User.objects.get(id=data['user_id'])
        data['related_booking'] = Booking.objects.get(id=data['booking_id'])
        del data['user_id']
        del data['booking_id']

        store_message_success = Message.add_message(**data)
        response = {}

        if store_message_success and SendToTopic.send_message_to_subscribers(data['from_user'].id,
                                                                             data['related_booking'].related_topic_name,
                                                                             data['message_text']):
            response['success'] = True
        else:
            response['success'] = False

        return JsonResponse(response)


# /getAllMessages
class GetAllMessages(APIView):
    def get(self, request):
        booking_id = request.GET.get('booking_id')

        messages = Message.get_all_messages(booking_id)
        response = {}

        if messages is not False:
            response['success'] = True
            response['messages'] = [model_to_dict(message, exclude='_state') for message in messages]
        else:
            response['success'] = False

        return JsonResponse(response)


# /message/
class SendToTopic(APIView):
    @staticmethod
    def send_message_to_subscribers(from_user, to_topic, message):
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

        fcm_json_response = json.loads(fcm_response.content)
        if fcm_json_response.has_key('error'):
            return False
        else:
            return True

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

