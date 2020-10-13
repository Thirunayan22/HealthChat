import os
location = "drawable/"

with open("drawable_country_names.txt","r") as file:
    alpha = file.readlines()

for countries in alpha:
    country = countries.split(",")[0].split(".")[2]
    with open("xml_country_names.txt","a") as file:
        file.write("<item>"+country+"</item>\n")
# for country in os.listdir(location):
