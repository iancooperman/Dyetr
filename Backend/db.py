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