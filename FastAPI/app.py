from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import xml.etree.ElementTree as ET
import configparser

config = configparser.ConfigParser()
config.read('config.ini')
ARQUIVO_XML = config['DEFAULT'].get('arquivo', 'estoque.xml')

app = FastAPI()

class Aparelho(BaseModel):
    id: int
    nome: str
    marca: str
    preco: float
    categoria: str
    deposito: str

def carregar_aparelhos_xml():
    try:
        tree = ET.parse(ARQUIVO_XML)
        root = tree.getroot()
        aparelhos = []
        for aparelho_node in root.findall("aparelho"):
            aparelhos.append({
                "id": int(aparelho_node.find("id").text),
                "nome": aparelho_node.find("nome").text,
                "marca": aparelho_node.find("marca").text,
                "preco": float(aparelho_node.find("preco").text),
                "categoria": aparelho_node.find("categoria").text,
                "deposito": aparelho_node.find("deposito").text,
            })
        return aparelhos
    except FileNotFoundError:
        root = ET.Element("estoque")
        tree = ET.ElementTree(root)
        tree.write(ARQUIVO_XML, encoding="utf-8", xml_declaration=True)
        return []

def salvar_aparelhos_xml(aparelhos):
    root = ET.Element("estoque")
    for aparelho in aparelhos:
        aparelho_elem = ET.SubElement(root, "aparelho")
        ET.SubElement(aparelho_elem, "id").text = str(aparelho["id"])
        ET.SubElement(aparelho_elem, "nome").text = aparelho["nome"]
        ET.SubElement(aparelho_elem, "marca").text = aparelho["marca"]
        ET.SubElement(aparelho_elem, "preco").text = str(aparelho["preco"])
        ET.SubElement(aparelho_elem, "categoria").text = aparelho["categoria"]
        ET.SubElement(aparelho_elem, "deposito").text = aparelho["deposito"]
    tree = ET.ElementTree(root)
    tree.write(ARQUIVO_XML, encoding="utf-8", xml_declaration=True)

@app.post("/aparelhos", status_code=201)
def adicionar_aparelho(aparelho: Aparelho):
    aparelhos = carregar_aparelhos_xml()
    if any(a["id"] == aparelho.id for a in aparelhos):
        raise HTTPException(status_code=400, detail="ID já existe.")
    aparelhos.append(aparelho.dict())
    salvar_aparelhos_xml(aparelhos)
    return {"erro": False, "message": "Aparelho adicionado com sucesso"}

@app.get("/aparelhos")
def listar_aparelhos():
    return carregar_aparelhos_xml()

@app.get("/aparelhos/{id}")
def buscar_aparelho(id: int):
    aparelhos = carregar_aparelhos_xml()
    for aparelho in aparelhos:
        if aparelho["id"] == id:
            return aparelho
    raise HTTPException(status_code=404, detail="Aparelho não encontrado.")

@app.put("/aparelhos/{id}")
def atualizar_aparelho(id: int, aparelho_atualizado: Aparelho):
    aparelhos = carregar_aparelhos_xml()
    for i, aparelho in enumerate(aparelhos):
        if aparelho["id"] == id:
            aparelhos[i] = aparelho_atualizado.dict()
            salvar_aparelhos_xml(aparelhos)
            return {"erro": False, "message": "Aparelho atualizado com sucesso"}
    raise HTTPException(status_code=404, detail="Aparelho não encontrado.")

@app.delete("/aparelhos/{id}")
def deletar_aparelho(id: int):
    aparelhos = carregar_aparelhos_xml()
    aparelhos_originais_len = len(aparelhos)
    aparelhos_atualizados = [a for a in aparelhos if a["id"] != id]
    if len(aparelhos_atualizados) == aparelhos_originais_len:
        raise HTTPException(status_code=404, detail="Aparelho não encontrado.")
    salvar_aparelhos_xml(aparelhos_atualizados)
    return {"erro": False, "message": "Aparelho removido com sucesso"}

@app.put("/aparelhos/{id}/transferir/{novo_deposito}")
def transferir_aparelho(id: int, novo_deposito: str):
    aparelhos = carregar_aparelhos_xml()
    for aparelho in aparelhos:
        if aparelho["id"] == id:
            aparelho["deposito"] = novo_deposito
            salvar_aparelhos_xml(aparelhos)
            return {"erro": False, "message": "Aparelho transferido com sucesso"}
    raise HTTPException(status_code=404, detail="Aparelho não encontrado.")