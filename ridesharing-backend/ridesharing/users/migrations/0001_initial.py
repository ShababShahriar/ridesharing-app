# -*- coding: utf-8 -*-
# Generated by Django 1.10.6 on 2017-03-28 02:22
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('email', models.EmailField(max_length=254, null=True, unique=True)),
                ('phone', models.IntegerField(default=0, unique=True)),
                ('is_active', models.BooleanField(default=True)),
                ('is_staff', models.BooleanField(default=False)),
                ('is_superuser', models.BooleanField(default=False)),
                ('display_name', models.CharField(blank=True, max_length=20)),
                ('reg_token', models.CharField(max_length=200)),
                ('activation_key', models.CharField(blank=True, max_length=40)),
                ('key_expires_at', models.DateTimeField(null=True)),
                ('phone_verified', models.BooleanField(default=False)),
                ('unverified_phone', models.IntegerField(null=True)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
        ),
    ]
