import uuid
from neo4j import GraphDatabase
from flask import Flask, request

app = Flask(__name__)


CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'

db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))


# URL params should be: /food_eaten?user_id=......&food_id=......
@app.route('/api/v1/food_liked', methods=["GET", "POST", "DELETE"])
def add_food():
    if request.method() == "GET":
        if request.args:

            if "user_id" in args:
                
                user_id = args["user_id"]

                eat_time = str(datetime.now())
                all_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND (u)-[:LIKES]->(f) RETURN collect(f)")

                # Above query (beautified):
                # MATCH (u: User), (f: food)
                # WHERE u.id = {user_id} AND (u)-[:LIKES]->(f)
                # RETURN collect(f)

                with db_driver.session() as session:
                    result = session.run(all_food_liked)

                return result, 201

    elif request.method() == "POST":
        if request.args:

            if "user_id" in args:
                if "food_id" in args:
                    
                    user_id = args["user_id"]
                    food_id = args["food_id"]

                    new_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND f.id = {food_id} CREATE (u)-[:LIKES]->(f)")

                    # Above query (beautified):
                    # MATCH (u: User), (f: food)
                    # WHERE u.id = {user_id} AND f.id = {food_id}
                    # CREATE (u)-[:LIKES]->(f)

                    with db_driver.session() as session:
                        session.run(new_food_liked)

                    return '', 201

    elif request.method() == "DELETE":
        if request.args:

            if "user_id" in args:
                if "food_id" in args:

                    user_id = args["user_id"]
                    food_id = args["food_id"]

                    food_deleted = (f"MATCH (u: User)-[r:LIKES]->(f: food) WHERE u.id = {user_id} AND f.id = {food_id} DELETE r")

                    # Above query (beautified):
                    # MATCH (u: User)-[r:LIKES]->(f: food)
                    # WHERE u.id = {user_id} AND f.id = {food_id}
                    # DELETE r

                    with db_driver.session() as session:
                        session.run(food_deleted)

                    return '', 201

    


app.run()
db_driver.close()