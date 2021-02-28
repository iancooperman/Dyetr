import uuid
from neo4j import GraphDatabase
from flask import Flask, request
from datetime import datetime

app = Flask(__name__)


CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'

db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))


# URL params should be: /food_eaten?user_id=......&food_id=......&meal_type=......
@app.route('/api/v1/food_eaten', methods=["GET", "POST"])
def add_food():
    if request.method() == "GET":
        if request.args:

            if "user_id" in args:
                
                user_id = args["user_id"]

                eat_time = str(datetime.now())
                all_food_eaten = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND (u)-[:ATE]->(f) RETURN collect(f)")

                # Above query (beautified):
                # MATCH (u: User), (f: food)
                # WHERE u.id = {user_id} AND (u)-[:ATE]->(f)
                # RETURN collect(f)

                with db_driver.session() as session:
                    result = session.run(all_food_eaten)

                return result, 201

    elif request.method() == "POST":
        if request.args:

            if "user_id" in args:
                if "food_id" in args:
                    if "meal_type" in args:
                    
                        user_id = args["user_id"]
                        food_id = args["food_id"]
                        meal_type = args["meal_type"]

                        eat_time = str(datetime.now())
                        new_food_eaten = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND f.id = {food_id} CREATE (u)-[:ATE {{timestamp: {eat_time}, meal: {meal_type}}}]->(f)")

                        # Above query (beautified):
                        # MATCH (u: User), (f: food)
                        # WHERE u.id = {user_id} AND f.id = {food_id}
                        # CREATE (u)-[:ATE {{timestamp: {eat_time}, meal: {meal_type}}}]->(f)

                        with db_driver.session() as session:
                            session.run(new_food_eaten)
                
                        return '', 201

    


app.run()
db_driver.close()