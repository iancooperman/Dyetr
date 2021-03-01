from neo4j import GraphDatabase


class db:
    def __init__(self, connection_string: str, auth: tuple):
        self._db_driver = GraphDatabase.driver(connection_string, auth=auth)

    """RETURNS: singular food entity corresponding to the id or none"""
    def find_food_item_by_id(self, id:str):
        query = 'MATCH(f:food) WHERE f.id = $id RETURN f'
        with self._db_driver.session() as session:
            result = session.run(query, id = id).single()

            if result:
                return result.data()['f']
            else:
                None

    """RETURNS: None
       CREATES: New food item"""
    def create_food_tem(self, food_id, item_name, calories, carbs, fat, protein):
        query = 'CREATE (f:food {id: $id, name: $name, calories: $calories, carbohydrates: $carbohydrates,  fat: $fat, protein: $protein})'
        with self._db_driver.session() as session:
            session.run(query, id = food_id, name = item_name, calories = calories, carbohydrates = carbs, fat = fat, protein = protein)
            
    """RETURNS: list of food items eaten by a provided user"""
    def find_food_eaten_by_user(self, user_id: str):
        all_food_eaten = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND (u)-[:ATE]->(f) RETURN collect(f)")

        # Above query (beautified):
        # MATCH (u: User), (f: food)
        # WHERE u.id = {user_id} AND (u)-[:ATE]->(f)
        # RETURN collect(f)

        with self._db_driver.session() as session:
            result = session.run(all_food_eaten)

        return result
    
    """RETURNS: None
       CREATES: New ATE relationship between provided user and food"""
    def create_ate_relationship(self, user_id: str, food_id: str, eat_time: str, meal_type: str):
        new_food_eaten = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND f.id = {food_id} CREATE (u)-[:ATE {{timestamp: {eat_time}, meal: {meal_type}}}]->(f)")

        # Above query (beautified):
        # MATCH (u: User), (f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # CREATE (u)-[:ATE {{timestamp: {eat_time}, meal: {meal_type}}}]->(f)

        with db_driver.session() as session:
            session.run(new_food_eaten)
    
    """RETURNS: list of food items liked by a provided user"""
    def find_food_liked_by_user(self, user_id: str):
        all_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND (u)-[:LIKES]->(f) RETURN collect(f)")

        # Above query (beautified):
        # MATCH (u: User), (f: food)
        # WHERE u.id = {user_id} AND (u)-[:LIKES]->(f)
        # RETURN collect(f)

        with db_driver.session() as session:
            result = session.run(all_food_liked)

        return result
    
    """RETURNS: None
       CREATES: New LIKES relationship between provided user and food"""
    def create_likes_relationship(self, user_id: str, food_id: str):
        new_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = {user_id} AND f.id = {food_id} CREATE (u)-[:LIKES]->(f)")

        # Above query (beautified):
        # MATCH (u: User), (f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # CREATE (u)-[:LIKES]->(f)

        with db_driver.session() as session:
            session.run(new_food_liked)
    
    """RETURNS: None
       DELETES: An existing LIKES relationship between provided user and food"""
    def delete_likes_relationship(self, user_id: str, food_id: str):
        food_deleted = (f"MATCH (u: User)-[r:LIKES]->(f: food) WHERE u.id = {user_id} AND f.id = {food_id} DELETE r")

        # Above query (beautified):
        # MATCH (u: User)-[r:LIKES]->(f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # DELETE r

        with db_driver.session() as session:
            session.run(food_deleted)
