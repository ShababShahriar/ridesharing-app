import json

from django.http import JsonResponse
from rest_framework.views import APIView
from django.forms.models import model_to_dict

from ridesharing.users.models import User
from ridesharing.bookings.models import Booking, UserBooking


# /addbooking/
class AddNewBooking(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        data['created_by'] = User.objects.get(id=data['user_id'])
        del data['user_id']

        response = {}

        booking_id = Booking.add_booking(**data)

        if booking_id:
            response['success'] = True
            response['booking_id'] = booking_id
        else:
            response['success'] = False

        return JsonResponse(response)


# /joinbooking/
class JoinBooking(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))
        response = {}

        data['user'] = User.objects.get(id=data['user_id'])
        data['booking'] = Booking.objects.get(id=data['booking_id'])

        if data['booking'].is_full:
            response['success'] = False
            return JsonResponse(response)

        data['booking'].add_new_user()
        del data['user_id']
        del data['booking_id']

        if UserBooking.add_user_booking(**data):
            response['success'] = True
        else:
            response['success'] = False

        return JsonResponse(response)


# /getActiveBookingList
class GetActiveBookingList(APIView):
    def get(self, request):
        params = request.GET
        param_keys = [key for key in params]
        data = {}

        for param_key in param_keys:
            if params[param_key]:
                data[param_key] = params[param_key]

        booking_list = Booking.get_active_booking_list(**data)

        response = {
            'bookings': [model_to_dict(booking, exclude='_state') for booking in booking_list]
        }

        return JsonResponse(response)
