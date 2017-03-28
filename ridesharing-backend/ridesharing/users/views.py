import json

from django.http import JsonResponse
from rest_framework.views import APIView

from models import User


# /login/
class Login(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        user, created = User.login(**data)
        response = {}

        if created or user:
            response['success'] = True
        else:
            response['success'] = False

        return JsonResponse(response)


# /updateToken/
class StoreRegistrationToken(APIView):
    def post(self, request):
        data = json.loads(json.dumps(request.data))

        response = User.update_registration_token(data)

        return JsonResponse({'success': response})
