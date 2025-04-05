// extension.js
const vscode = require("vscode");

const OUTPUT_CHANNEL = vscode.window.createOutputChannel(
  "Product Flags Updater"
);

function activate(context) {
  let disposable = vscode.workspace.onDidChangeTextDocument((event) => {
    const document = event.document;

    // Only process JSON files
    if (document.languageId !== "json") {
      return;
    }

    try {
      // Parse the JSON content
      const fullText = document.getText();
      const jsonContent = JSON.parse(fullText);

      if (!jsonContent.products) {
        return;
      }

      let modified = false;

      // Get the changes
      event.contentChanges.forEach((change) => {
        // Get the context of the change
        const startLine = Math.max(0, change.range.start.line - 5);
        const endLine = Math.min(
          document.lineCount - 1,
          change.range.end.line + 5
        );

        let currentProduct = null;

        // Find current product context
        for (let i = startLine; i >= 0; i--) {
          const line = document.lineAt(i).text.trim();
          const skuMatch = line.match(/\"([^\"]+_HU03)\"\s*:\s*\{/);
          if (skuMatch) {
            currentProduct = skuMatch[1];
            break;
          }
        }

        if (currentProduct && jsonContent.products[currentProduct]) {
          const product = jsonContent.products[currentProduct];
          const changedLine = document.lineAt(change.range.start.line).text;

          // Title changes - strict check
          if (changedLine.includes('"product_name"')) {
            product.update_flags.title = true;
            modified = true;
          }

          // Category changes - check exact location
          if (changedLine.includes('"categories"')) {
            const adjacentLines = document.getText(
              new vscode.Range(
                change.range.start.line - 1,
                0,
                change.range.start.line + 1,
                document.lineAt(
                  Math.min(change.range.start.line + 1, document.lineCount - 1)
                ).text.length
              )
            );

            if (adjacentLines.includes('"categories"')) {
              product.update_flags.category = true;
              modified = true;
            }
          }

          // Images changes - using the working logic
          if (
            changedLine.includes("files") ||
            changedLine.includes("urls") ||
            changedLine.includes("count") ||
            changedLine.includes('"images"')
          ) {
            product.update_flags.images = true;
            modified = true;
          }
        }
      });

      // If modifications were made, update the file
      if (modified) {
        const edit = new vscode.WorkspaceEdit();
        const formattedJson = JSON.stringify(jsonContent, null, 2);

        edit.replace(
          document.uri,
          new vscode.Range(0, 0, document.lineCount, 0),
          formattedJson
        );

        vscode.workspace.applyEdit(edit).then((success) => {
          if (!success) {
            vscode.window.showErrorMessage("Failed to update product flags");
          }
        });
      }
    } catch (error) {
      OUTPUT_CHANNEL.appendLine(`Error: ${error.message}`);
      vscode.window.showErrorMessage(
        "Error updating product flags: " + error.message
      );
    }
  });

  context.subscriptions.push(disposable);
}

function deactivate() {
  OUTPUT_CHANNEL.dispose();
}

module.exports = {
  activate,
  deactivate,
};
