import json

from django.http import JsonResponse
from rest_framework.views import APIView

from ridesharing.users.models import User
from ridesharing.bookings.models import Booking, UserBooking


# /addbooking/
class AddNewBooking(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        data['created_by'] = User.objects.get(id=data['user_id'])
        del data['user_id']

        response = {}

        if Booking.add_booking(**data):
            response['success'] = True
        else:
            response['success'] = False

        return JsonResponse(response)


# /joinbooking/
class JoinBooking(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        data['user'] = User.objects.get(id=data['user_id'])
        data['booking'] = Booking.objects.get(id=data['booking_id'])
        data['booking'].add_new_user()
        del data['user_id']
        del data['booking_id']

        response = {}

        if UserBooking.add_user_booking(**data):
            response['success'] = True
        else:
            response['success'] = False

        return JsonResponse(response)