import tkinter as tk
from tkinter import filedialog, messagebox
from PyPDF2 import PdfReader, PdfWriter
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
from io import BytesIO

class PDFEditorApp:
    def __init__(self, master):
        self.master = master
        self.master.title("Simple PDF Editor")
        self.master.geometry("600x400")

        self.pdf_path = None
        self.current_page = 0
        self.pdf_content = []
        self.pdf_pages = []

        # Create UI elements
        self.create_widgets()

    def create_widgets(self):
        # File selection
        self.select_button = tk.Button(self.master, text="Select PDF", command=self.select_pdf)
        self.select_button.pack(pady=10)

        # Text area for editing
        self.text_area = tk.Text(self.master, height=15, width=70)
        self.text_area.pack(pady=10)

        # Navigation buttons
        self.prev_button = tk.Button(self.master, text="Previous Page", command=self.prev_page)
        self.prev_button.pack(side=tk.LEFT, padx=10)

        self.next_button = tk.Button(self.master, text="Next Page", command=self.next_page)
        self.next_button.pack(side=tk.RIGHT, padx=10)

        # Save button
        self.save_button = tk.Button(self.master, text="Save Changes", command=self.save_changes)
        self.save_button.pack(side=tk.BOTTOM, pady=10)

    def select_pdf(self):
        self.pdf_path = filedialog.askopenfilename(filetypes=[("PDF files", "*.pdf")])
        if self.pdf_path:
            self.load_pdf()

    def load_pdf(self):
        reader = PdfReader(self.pdf_path)
        self.pdf_content = [page.extract_text() for page in reader.pages]
        self.pdf_pages = reader.pages
        self.current_page = 0
        self.display_current_page()

    def display_current_page(self):
        self.text_area.delete('1.0', tk.END)
        self.text_area.insert(tk.END, self.pdf_content[self.current_page])

    def prev_page(self):
        if self.current_page > 0:
            self.update_current_page_content()
            self.current_page -= 1
            self.display_current_page()

    def next_page(self):
        if self.current_page < len(self.pdf_content) - 1:
            self.update_current_page_content()
            self.current_page += 1
            self.display_current_page()

    def update_current_page_content(self):
        self.pdf_content[self.current_page] = self.text_area.get('1.0', tk.END).strip()

    def save_changes(self):
        if not self.pdf_path:
            messagebox.showerror("Error", "No PDF loaded")
            return

        self.update_current_page_content()

        output = PdfWriter()

        for i, page in enumerate(self.pdf_pages):
            packet = BytesIO()
            can = canvas.Canvas(packet, pagesize=letter)
            
            # Get the original page size
            page_width = float(page.mediabox.width)
            page_height = float(page.mediabox.height)
            
            # Set the canvas size to match the original page
            can.setPageSize((page_width, page_height))
            
            # Draw the text
            can.drawString(10, page_height - 20, self.pdf_content[i])
            can.save()
            
            packet.seek(0)
            new_pdf = PdfReader(packet)
            page.merge_page(new_pdf.pages[0])
            output.add_page(page)

        # Save the modified PDF
        output_path = filedialog.asksaveasfilename(defaultextension=".pdf", filetypes=[("PDF files", "*.pdf")])
        if output_path:
            with open(output_path, "wb") as output_file:
                output.write(output_file)
            messagebox.showinfo("Success", "PDF saved successfully")

if __name__ == "__main__":
    root = tk.Tk()
    app = PDFEditorApp(root)
    root.mainloop()