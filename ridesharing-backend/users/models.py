from django.db.models import *

from config import settings


class User(Model):
    email = EmailField(unique=True, null=True)
    phone = IntegerField(unique=True, default=None)
    is_active = BooleanField(default=True)
    is_staff = BooleanField(default=False)
    is_superuser = BooleanField(default=False)
    display_name = CharField(max_length=settings.NAME_MAX_LENGTH, default=None)

    reg_token = CharField(max_length=200)
    activation_key = CharField(max_length=40, blank=True)
    key_expires_at = DateTimeField(null=True)
    phone_verified = BooleanField(default=False)
    unverified_phone = IntegerField(null=True)

    @staticmethod
    def login(**kwargs):
        user, created = User.objects.get_or_create(**kwargs)
        return user, created
