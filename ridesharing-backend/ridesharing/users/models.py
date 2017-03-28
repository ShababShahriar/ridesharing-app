from django.db.models import *

from config import settings


class User(Model):
    email = EmailField(unique=True, null=True)
    phone = IntegerField(unique=True, default=0)
    is_active = BooleanField(default=True)
    is_staff = BooleanField(default=False)
    is_superuser = BooleanField(default=False)
    display_name = CharField(max_length=settings.NAME_MAX_LENGTH, blank=True)

    reg_token = CharField(max_length=200)
    activation_key = CharField(max_length=40, blank=True)
    key_expires_at = DateTimeField(null=True)
    phone_verified = BooleanField(default=False)
    unverified_phone = IntegerField(null=True)
    created_at = DateTimeField(auto_now_add=True)

    @staticmethod
    def login(**kwargs):
        user, created = User.objects.get_or_create(**kwargs)
        return user, created

    @staticmethod
    def update_registration_token(data):
        try:
            user = User.objects.get(id=data['id'])
            user.reg_token = data['reg_token']
            user.save()
            return True
        except:
            return False

